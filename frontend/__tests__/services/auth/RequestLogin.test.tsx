import MockAdapter from "axios-mock-adapter";
import * as SecureStore from "expo-secure-store";
import { requestLogin } from "../../../services/auth/requestLogin";
import { apiUrl } from "../../../services/fetchWelcome";
import { describe, it, expect, afterEach } from "@jest/globals";
import api from "../../../services/interceptors/DeviceInterceptor"; // Adjust path as necessary

// Setup axios mock
const mock = new MockAdapter(api);

// Mock SecureStore
jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));


describe("requestLogin", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in successfully on android and stores token", async () => {
        const token = "mock-token";
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });

        const result = await requestLogin("user@example.com", "password123", "web");

        expect(result).toEqual({ success: true });
        expect(SecureStore.setItemAsync).toHaveBeenCalledWith("authToken", token);
    });


    it("returns success: false for non-200 status", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(404,{});

        const result = await requestLogin("wrong@example.com", "wrongpass", "web");

        expect(result).toEqual({ success: false });
    });

    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/login`).networkError();

        await expect(requestLogin("x@x.com", "fail", "WEB")).rejects.toMatchObject({
            status: 500,
            message: "Network Error"
        });
    });
});
