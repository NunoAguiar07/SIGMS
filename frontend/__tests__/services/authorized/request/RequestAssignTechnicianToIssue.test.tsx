import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {requestAssignTechnicianToIssue} from "../../../../services/authorized/RequestAssignTechnicianToIssue";

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

describe("requestAssignTechnicianToIssue after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully assigns technician to issue", async () => {
        const email = "tech@example.com";
        const password = "password";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const issueId = 101;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const login = await requestLogin(email, password, deviceType);
        expect(login).toBe(true);

        // Mock technician assignment
        mock.onPut(`/issue-reports/${issueId}/assign`).reply(200);

        const result = await requestAssignTechnicianToIssue(issueId);
        expect(result).toBe(true);
    });

    it("logs in but fails to assign technician due to network error", async () => {
        const email = "fail@example.com";
        const password = "badpass";
        const deviceType = "WEB";
        const issueId = 202;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate failure
        mock.onPut(`/issue-reports/${issueId}/assign`).networkError();

        await expect(requestAssignTechnicianToIssue(issueId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });

    it("returns false if response is not 200", async () => {
        const email = "user@example.com";
        const password = "pass";
        const deviceType = "WEB";
        const issueId = 303;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate unexpected status
        mock.onPut(`/issue-reports/${issueId}/assign`).reply(204); // No Content

        const result = await requestAssignTechnicianToIssue(issueId);
        expect(result).toBe(false);
    });
});
