import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { IssueReportInterface } from "../../../../types/IssueReportInterface";
import {fetchIssueReportsByRoom} from "../../../../services/authorized/FetchIssueReportsByRoom";

// Minimal RoomInterface for test purposes
interface RoomInterface {
    id: number;
    name: string;
    capacity: number;
    type: string;
}

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

describe("fetchIssueReportsByRoom after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches issues for a room", async () => {
        const email = "user@example.com";
        const password = "validpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const roomId = 200;

        const mockedIssues: IssueReportInterface[] = [
            {
                id: 1,
                description: "Whiteboard missing markers",
                userId: 10,
                room: { id: roomId, name: "Room 200", capacity: 20 },
                assignedTo: 12,
            },
            {
                id: 2,
                description: "Chair broken",
                userId: 11,
                room: { id: roomId, name: "Room 200", capacity: 20},
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock room issue fetch
        mock
            .onGet(`rooms/${roomId}/issues`)
            .reply(200, { data: mockedIssues });

        const result = await fetchIssueReportsByRoom(roomId);
        expect(result).toEqual(mockedIssues);
    });

    it("logs in but fails to fetch room issues", async () => {
        const email = "failuser@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const roomId = 300;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error
        mock
            .onGet(`rooms/${roomId}/issues`)
            .networkError();

        await expect(fetchIssueReportsByRoom(roomId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
