
import MockAdapter from "axios-mock-adapter";
import { requestRegister } from "../../../services/auth/requestRegister";
import { apiUrl } from "../../../services/fetchWelcome";
import { describe, it, expect, afterEach } from "@jest/globals";
import { handleAxiosError } from "../../../utils/HandleAxiosError";
import api from "../../../services/interceptors/DeviceInterceptor";

// Mock axios
const mock = new MockAdapter(api);

// Mock error handler
jest.mock("../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

describe("requestRegister", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    const testPayload = {
        email: "user@example.com",
        username: "newuser",
        password: "pass123",
        role: "STUDENT",
        universityId: 42,
    };

    it("returns token on 201 Created", async () => {
        const mockMessage = "message";
        mock.onPost(`${apiUrl}auth/register`, testPayload)
            .reply(201, { message: mockMessage });

        const result = await requestRegister(
            testPayload.email,
            testPayload.username,
            testPayload.password,
            testPayload.role,
            testPayload.universityId
        );

        expect(result).toBe(mockMessage);
    });

    it("returns undefined for non-201 status", async () => {
        mock
            .onPost(`${apiUrl}auth/register`, testPayload)
            .reply(400, { error: "Bad Request" });

       await expect(
            requestRegister(
                testPayload.email,
                testPayload.username,
                testPayload.password,
                testPayload.role,
                testPayload.universityId
            )
        ).rejects.toMatchObject({
            status: 400,
            message: "Request failed with status code 400",
        });
       });


    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/register`).networkError();

        await expect(
            requestRegister(
                testPayload.email,
                testPayload.username,
                testPayload.password,
                testPayload.role,
                testPayload.universityId
            )
        ).rejects.toMatchObject({ status: 500, message: "Network Error" });

        expect(handleAxiosError).toHaveBeenCalled();
    });
});