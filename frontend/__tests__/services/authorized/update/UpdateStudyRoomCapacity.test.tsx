import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {updateStudyRoomCapacity} from "../../../../services/authorized/UpdateStudyRoomCapacity";

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

describe("updateStudyRoomCapacity after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and updates study room capacity successfully", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin("user@example.com", "password123", "web");
        expect(loginResult.success).toBe(true);

        mock.onPut("/devices/update", { withCredentials: true }).reply(200);

        await expect(updateStudyRoomCapacity()).resolves.toBeUndefined();
    });

    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "password123", "web");

        mock.onPut("/devices/update").networkError();

        await expect(updateStudyRoomCapacity()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
