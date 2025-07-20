import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { ProfileInterface } from "../../../../types/ProfileInterface";
import {fetchProfile} from "../../../../services/authorized/FetchProfile";

// Mock SecureStore for login
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

describe("fetchProfile after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches profile data", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const mockedProfile: ProfileInterface = {
            username: "johndoe",
            email: "john.doe@example.com",
            image: [137, 80, 78, 71], // sample PNG file header bytes or any number array
            university: "University of Example",
        };

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock profile fetch
        mock.onGet("/profile").reply(200, mockedProfile);

        const result = await fetchProfile();
        expect(result).toEqual(mockedProfile);
    });

    it("logs in but fails to fetch profile data", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error
        mock.onGet("/profile").networkError();

        await expect(fetchProfile()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
