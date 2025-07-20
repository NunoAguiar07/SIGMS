import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {requestFixIssue} from "../../../../services/authorized/RequestFixIssue";

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

describe("requestFixIssue after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully fixes the issue (204 No Content)", async () => {
        const email = "tech@example.com";
        const password = "pass123";
        const deviceType = "WEB";
        const issueId = 321;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult).toBe(true);

        // Mock delete request
        mock.onDelete(`issue-reports/${issueId}/delete`).reply(204);

        const result = await requestFixIssue(issueId);
        expect(result).toBe(true);
    });

    it("returns false if delete response status is not 204", async () => {
        const issueId = 123;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("test@example.com", "testpass", "WEB");

        mock.onDelete(`issue-reports/${issueId}/delete`).reply(200); // Wrong status

        const result = await requestFixIssue(issueId);
        expect(result).toBe(false);
    });

    it("throws parsed error if network fails", async () => {
        const issueId = 456;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("test@example.com", "testpass", "WEB");

        mock.onDelete(`issue-reports/${issueId}/delete`).networkError();

        await expect(requestFixIssue(issueId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
