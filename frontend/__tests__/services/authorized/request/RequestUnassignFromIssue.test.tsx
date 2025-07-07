import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {requestUnassignFromIssue} from "../../../../services/authorized/RequestUnassignFromIssue";

// Mock SecureStore for auth token
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

describe("requestUnassignFromIssue after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully unassigns from an issue (204)", async () => {
        const email = "tech@example.com";
        const password = "securePass";
        const deviceType = "web";
        const issueId = 101;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult.success).toBe(true);

        // Mock unassignment
        mock
            .onPut(`issue-reports/${issueId}/unassign`)
            .reply(204);

        const result = await requestUnassignFromIssue(issueId);
        expect(result).toBe(true);
    });

    it("returns false if response status is not 204", async () => {
        const issueId = 102;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`issue-reports/${issueId}/unassign`)
            .reply(200); // Not 204

        const result = await requestUnassignFromIssue(issueId);
        expect(result).toBe(false);
    });

    it("throws a parsed error on network failure", async () => {
        const issueId = 103;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`issue-reports/${issueId}/unassign`)
            .networkError();

        await expect(requestUnassignFromIssue(issueId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
