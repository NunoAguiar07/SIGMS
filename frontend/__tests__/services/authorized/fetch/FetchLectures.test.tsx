import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { Lecture } from "../../../../types/calendar/Lecture";
import {fetchLectures} from "../../../../services/authorized/FetchLectures";

// Mock SecureStore
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

describe("fetchLectures after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches lectures", async () => {
        const email = "user@example.com";
        const password = "testpass";
        const deviceType = "web";
        const token = "fake-jwt-token";

        const mockedLectures: Lecture[] = [
            {
                id: 1,
                weekDay: 1,
                startTime: "08:00",
                endTime: "09:30",
                type: "LECTURE",
                room: {
                    id: 101,
                    name: "Room A",
                },
                schoolClass: {
                    id: 201,
                    name: "1A",
                    subject: {
                        name: "Mathematics",
                    },
                },
                effectiveFrom: "2025-09-01",
                effectiveUntil: "2026-01-31",
            },
            {
                id: 2,
                weekDay: 3,
                startTime: "10:00",
                endTime: "11:30",
                type: "EXERCISE",
                room: {
                    id: 102,
                    name: "Room B",
                },
                schoolClass: {
                    id: 202,
                    name: "1B",
                    subject: {
                        name: "Physics",
                    },
                },
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse.success).toBe(true);

        // Mock lectures fetch
        mock.onGet("/lectures").reply(200, { data: mockedLectures });

        const result = await fetchLectures();
        expect(result).toEqual(mockedLectures);
    });

    it("logs in but fails to fetch lectures", async () => {
        const email = "error@example.com";
        const password = "wrongpass";
        const deviceType = "web";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error
        mock.onGet("/lectures").networkError();

        await expect(fetchLectures()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
