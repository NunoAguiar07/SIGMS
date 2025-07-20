import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { LectureType } from "../../../../types/welcome/FormCreateEntityValues";
import {createLecture} from "../../../../services/authorized/CreateLecture";

// Mock SecureStore for auth flow
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

describe("createLecture after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully creates a lecture", async () => {
        const email = "teacher@example.com";
        const password = "secure123";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const lectureData = {
            schoolClassId: 10,
            roomId: 101,
            type: "LECTURE" as LectureType,
            weekDay: 2,
            startTime: "09:00",
            endTime: "10:30",
        };

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });

        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock successful lecture creation
        mock.onPost("lectures/add", lectureData).reply(201);

        const result = await createLecture(
            lectureData.schoolClassId,
            lectureData.roomId,
            lectureData.type,
            lectureData.weekDay,
            lectureData.startTime,
            lectureData.endTime
        );

        expect(result).toBe(true);
    });

    it("logs in but fails to create a lecture", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";

        const lectureData = {
            schoolClassId: 11,
            roomId: 102,
            type: "EXERCISE" as LectureType,
            weekDay: 4,
            startTime: "11:00",
            endTime: "12:00",
        };

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Mock failure to create lecture
        mock.onPost("lectures/add", lectureData).networkError();

        await expect(
            createLecture(
                lectureData.schoolClassId,
                lectureData.roomId,
                lectureData.type,
                lectureData.weekDay,
                lectureData.startTime,
                lectureData.endTime
            )
        ).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
