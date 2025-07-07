import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {fetchStudyRoomCapacity} from "../../../../services/authorized/FetchStudyRoomCapacity";
import {StudyRoomCapacity} from "../../../../types/StudyRoomCapacity";
import {Capacity} from "../../../../types/Capacity";

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

describe("fetchStudyRoomCapacity after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches study room capacities", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "web";
        const token = "fake-jwt-token";

        const mockedCapacities: StudyRoomCapacity[] = [
            {
                id: "room1",
                roomName: "Study Room 1",
                capacity: Capacity.LOW,
            },
            {
                id: "room2",
                roomName: "Study Room 2",
                capacity: Capacity.FULL,
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse.success).toBe(true);

        // Mock API fetch
        mock.onGet("/devices").reply(200, { data: mockedCapacities });

        const result = await fetchStudyRoomCapacity();
        expect(result).toEqual(mockedCapacities);
    });

    it("logs in but fails to fetch study room capacities", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "web";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error
        mock.onGet("/devices").networkError();

        await expect(fetchStudyRoomCapacity()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
