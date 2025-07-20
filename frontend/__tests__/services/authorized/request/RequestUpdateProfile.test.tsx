import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {requestUpdateProfile} from "../../../../services/authorized/RequestUpdateProfile";

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

describe("requestUpdateProfile after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and updates only the image successfully", async () => {
        const email = "user@example.com";
        const password = "password123";
        const deviceType = "WEB";
        const updatedImage = [255, 0, 255, 128]; // example image byte array

        // Mock login endpoint
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult).toBe(true);

        // Mock profile update endpoint returning full updated profile
        const updatedProfile = {
            username: "user123",
            email: "user@example.com",
            image: updatedImage,
            university: "Test University",
        };
        mock.onPut("/profile/update", { image: updatedImage }).reply(200, updatedProfile);

        const result = await requestUpdateProfile({ image: updatedImage });
        expect(result).toEqual(updatedProfile);
    });

    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "password123", "WEB");

        mock.onPut("/profile/update").networkError();

        await expect(requestUpdateProfile({ image: [0, 0, 0] })).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
