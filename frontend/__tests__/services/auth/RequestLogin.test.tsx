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

    it("logs in successfully on web and stores token", async () => {
        const token = "authToken";
        mock.onPost(`${apiUrl}auth/login`).reply(200, { accessToken: token });

        const result = await requestLogin("user@example.com", "Password123!", "WEB");

        expect(result).toBe(true);
    });



    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/login`).networkError();

        await expect(requestLogin("x@x.com", "fail", "WEB")).rejects.toMatchObject({
            status: 500,
            message: "Network Error"
        });
    });
});
