import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { RoomType } from "../../../../types/welcome/FormCreateEntityValues";
import {createRoom} from "../../../../services/authorized/CreateRoomSubject";


jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));


jest.mock("../../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

const mock = new MockAdapter(api);

describe("createRoom after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully creates a room", async () => {
        const email = "admin@example.com";
        const password = "admin123";
        const deviceType = "web";
        const token = "fake-jwt-token";

        const roomData = {
            name: "Room A1",
            capacity: 50,
            type: "LECTURE_HALL" as RoomType,
        };

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse.success).toBe(true);

        // Mock room creation success
        mock.onPost("/rooms/add", roomData).reply(201);

        const result = await createRoom(roomData.name, roomData.capacity, roomData.type);
        expect(result).toBe(true);
    });

    it("logs in but fails to create a room", async () => {
        const email = "admin2@example.com";
        const password = "adminfail";
        const deviceType = "web";

        const roomData = {
            name: "Room B2",
            capacity: 30,
            type: "SEMINAR_ROOM" as RoomType,
        };

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Mock room creation failure
        mock.onPost("/rooms/add", roomData).networkError();

        await expect(
            createRoom(roomData.name, roomData.capacity, roomData.type)
        ).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
