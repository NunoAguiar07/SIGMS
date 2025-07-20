import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { Lecture } from "../../../../types/calendar/Lecture";
import {updateLectureSchedule} from "../../../../services/authorized/RequestUpdateLecture";

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

describe("updateLectureSchedule after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    const lecture: Lecture = {
        id: 101,
        weekDay: 1,
        startTime: "10:00",
        endTime: "12:00",
        type: "LECTURE",
        room: {
            id: 5,
            name: "Room A",
        },
        schoolClass: {
            id: 1,
            name: "Class A",
            subject: {
                name: "Math",
            },
        },
    };

    it("logs in and successfully updates a lecture schedule (200)", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin("user@example.com", "pass", "WEB");
        expect(loginResult).toBe(true);

        mock
            .onPut(`lectures/${lecture.id}/update`, {
                newRoomId: lecture.room.id,
                newType: lecture.type,
                newWeekDay: lecture.weekDay,
                newStartTime: lecture.startTime,
                newEndTime: lecture.endTime,
                changeStartDate: "2025-07-01",
                changeEndDate: "2025-07-31",
            })
            .reply(200);

        const result = await updateLectureSchedule(lecture, "2025-07-01", "2025-07-31");
        expect(result).toBe(true);
    });

    it("returns false if response status is not 200", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "WEB");

        mock
            .onPut(`lectures/${lecture.id}/update`)
            .reply(204); // Not 200

        const result = await updateLectureSchedule(lecture, null, null);
        expect(result).toBe(false);
    });

    it("throws parsed error on network failure", async () => {
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "WEB");

        mock
            .onPut(`lectures/${lecture.id}/update`)
            .networkError();

        await expect(updateLectureSchedule(lecture, null, null)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
