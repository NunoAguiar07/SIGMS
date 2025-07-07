import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {leaveClass} from "../../../../services/authorized/RequestLeaveClass";

// Mock SecureStore for token
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

describe("leaveClass after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully leaves a class (204)", async () => {
        const email = "student@example.com";
        const password = "password123";
        const deviceType = "web";
        const token = "fake-jwt-token";
        const subjectId = 7;
        const classId = 13;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult.success).toBe(true);

        // Mock successful leave
        mock
            .onDelete(`/subjects/${subjectId}/classes/${classId}/users/remove`)
            .reply(204);

        const result = await leaveClass(subjectId, classId);
        expect(result).toBe(true);
    });

    it("returns false if leave response is not 204", async () => {
        const subjectId = 8;
        const classId = 14;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onDelete(`/subjects/${subjectId}/classes/${classId}/users/remove`)
            .reply(200); // not 204

        const result = await leaveClass(subjectId, classId);
        expect(result).toBe(false);
    });

    it("throws a parsed error on network failure", async () => {
        const subjectId = 9;
        const classId = 15;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onDelete(`/subjects/${subjectId}/classes/${classId}/users/remove`)
            .networkError();

        await expect(leaveClass(subjectId, classId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
