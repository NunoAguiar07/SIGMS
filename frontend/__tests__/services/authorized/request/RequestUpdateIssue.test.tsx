import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {requestUpdateIssue} from "../../../../services/authorized/RequestUpdateIssue";

// Mock SecureStore for token
jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));

// Mock handleAxiosError
jest.mock("../../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

const mock = new MockAdapter(api);

describe("requestUpdateIssue after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully updates an issue (200)", async () => {
        const email = "user@example.com";
        const password = "testpass";
        const deviceType = "web";
        const issueId = 123;
        const newDescription = "Updated issue description";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult.success).toBe(true);

        // Mock issue update
        mock
            .onPut(`issue-reports/${issueId}/update`, { description: newDescription })
            .reply(200);

        const result = await requestUpdateIssue(issueId, newDescription);
        expect(result).toBe(true);
    });

    it("returns false if response is not 200", async () => {
        const issueId = 456;
        const description = "Should not succeed";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`issue-reports/${issueId}/update`, { description })
            .reply(204); // Not 200

        const result = await requestUpdateIssue(issueId, description);
        expect(result).toBe(false);
    });

    it("throws parsed error on network failure", async () => {
        const issueId = 789;
        const description = "This will error";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`issue-reports/${issueId}/update`, { description })
            .networkError();

        await expect(requestUpdateIssue(issueId, description)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
