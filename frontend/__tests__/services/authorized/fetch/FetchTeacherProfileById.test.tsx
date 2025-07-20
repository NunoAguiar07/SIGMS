import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { TeacherUser } from "../../../../types/teacher/TeacherUser";
import {fetchTeacherProfileById} from "../../../../services/authorized/FetchTeacherProfileById";

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

describe("fetchTeacherProfileById after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches teacher profile", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const teacherId = 123;

        const mockedTeacherProfile: TeacherUser = {
            user: {
                id: teacherId,
                username: "profjones",
                email: "jones@example.edu",
                image: [137, 80, 78, 71],
                university: "Example University",
            },
            office: {
                room: {
                    id: 101,
                    name: "Office 1A",
                    capacity: 1,
                },
            },
        };

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        mock.onGet(`/profile/${encodeURIComponent(teacherId)}`).reply(200, mockedTeacherProfile);

        const result = await fetchTeacherProfileById(teacherId);
        expect(result).toEqual(mockedTeacherProfile);
    });

    it("logs in but fails to fetch teacher profile", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const teacherId = 999;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        mock.onGet(`/profile/${encodeURIComponent(teacherId)}`).networkError();

        await expect(fetchTeacherProfileById(teacherId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
