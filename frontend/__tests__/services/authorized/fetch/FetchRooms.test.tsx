import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { RoomInterface } from "../../../../types/RoomInterface";
import {fetchRooms} from "../../../../services/authorized/FetchRooms";

// Mock SecureStore
jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));

// Mock error handler
jest.mock("../../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

const mock = new MockAdapter(api);

describe("fetchRooms after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and fetches rooms without search query", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const mockedRooms: RoomInterface[] = [
            { id: 1, name: "Room A", capacity: 20},
            { id: 2, name: "Room B", capacity: 30 },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock fetch without search query
        mock.onGet("rooms").reply(200, { data: mockedRooms });

        const result = await fetchRooms("");
        expect(result).toEqual(mockedRooms);
    });

    it("logs in and fetches rooms with search query", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const searchQuery = "RoomB";

        const mockedRooms: RoomInterface[] = [
            { id: 2, name: "RoomB", capacity: 30 },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        await requestLogin(email, password, deviceType);

        // Mock fetch with search query (encoded)
        mock
            .onGet(`rooms?search=${encodeURIComponent(searchQuery)}`)
            .reply(200, { data: mockedRooms });

        const result = await fetchRooms(searchQuery);
        expect(result).toEqual(mockedRooms);
    });

    it("logs in but fetch fails with network error", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error on fetch
        mock.onGet("rooms").networkError();

        await expect(fetchRooms("")).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });

    it("returns empty array if response status is not 200", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        await requestLogin(email, password, deviceType);

        // Return status 204 No Content, expect empty array
        mock.onGet("rooms").reply(204);

        const result = await fetchRooms("");
        expect(result).toEqual([]);
    });
});
