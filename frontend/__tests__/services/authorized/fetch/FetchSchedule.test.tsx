import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { ScheduleApiResponse } from "../../../../types/ScheduleApiResponse";
import {fetchSchedule} from "../../../../services/authorized/FetchSchedule";

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

describe("fetchSchedule after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches schedule data", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "web";
        const token = "fake-jwt-token";

        const mockedSchedule: ScheduleApiResponse = {
            lectures: [
                {
                    lecture: {
                        id: 1,
                        weekDay: 1,
                        startTime: "08:00",
                        endTime: "09:30",
                        type: "LECTURE",
                        room: { id: 101, name: "Room A" },
                        schoolClass: {
                            id: 201,
                            name: "Class 1",
                            subject: { name: "Mathematics" },
                        },
                        effectiveFrom: "2025-01-01",
                        effectiveUntil: "2025-12-31",
                    },
                    teacher: [
                        {
                            user: {
                                id: 301,
                                username: "profsmith",
                                email: "smith@example.edu",
                                image: [137, 80, 78, 71],
                                university: "Example University",
                            },
                            office: {
                                room: {
                                    id: 401,
                                    name: "Office 101",
                                    capacity: 1,
                                },
                            },
                        },
                    ],
                },
            ],
        };

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse.success).toBe(true);

        mock.onGet("/schedule").reply(200, mockedSchedule);

        const result = await fetchSchedule();
        expect(result).toEqual(mockedSchedule);
    });

    it("logs in but fails to fetch schedule data", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "web";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        mock.onGet("/schedule").networkError();

        await expect(fetchSchedule()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
