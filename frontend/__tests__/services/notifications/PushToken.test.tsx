import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../services/auth/requestLogin";
import * as Notifications from "expo-notifications";
import { apiUrl } from "../../../services/fetchWelcome";
import {pushToken} from "../../../services/notifications/PushToken";

jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));

jest.mock("../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

// Mock expo notifications module
jest.mock("expo-notifications");

const mock = new MockAdapter(api);

describe("pushToken after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and posts the Expo push token successfully", async () => {
        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin("user@example.com", "password123", "web");
        expect(loginResult.success).toBe(true);

        // @ts-ignore
        (Notifications.getExpoPushTokenAsync as jest.Mock).mockResolvedValue({data: "ExponentPushToken[fake-token]",});

        // Mock POST to expoToken endpoint
        mock.onPost("expoToken").reply(config => {
            expect(config.headers!['X-Expo-Token']).toBe("ExponentPushToken[fake-token]");
            return [201];
        });

        const result = await pushToken();
        expect(result).toBe(true);
    });

    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "password123", "web");
        // @ts-ignore
        (Notifications.getExpoPushTokenAsync as jest.Mock).mockResolvedValue({data: "ExponentPushToken[fake-token]",});

        mock.onPost("expoToken").networkError();

        await expect(pushToken()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
