import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import {UniversityInterface} from "../../../types/UniversityInterface";
import {apiUrl} from "../../../services/fetchWelcome";
import {fetchUniversities} from "../../../services/fetchUniversities";
import {describe, it, expect, afterEach} from "@jest/globals";
import {requestLogin} from "../../../services/auth/requestLogin";


const mock = new MockAdapter(axios);


describe("fetchUniversities", () => {
    afterEach(() => {
        mock.reset();
    });

    it("returns data when status is 200", async () => {
        const fakeData: UniversityInterface[] = [
            { id: 1, name: "test"},
        ];

        mock
            .onGet(`${apiUrl}universities?search=test`)
            .reply(200, { data: fakeData });

        const result = await fetchUniversities("test");
        expect(result).toEqual(fakeData);
    });

    it("returns empty array when status is not 200", async () => {
        mock
            .onGet(`${apiUrl}universities?search=none`)
            .reply(200, { data: [] });

        const result = await fetchUniversities("none");
        expect(result).toEqual([]);
    });

    it("throws an error when request fails", async () => {
        mock.onGet(/universities/).networkError();

        await expect(fetchUniversities("x")).rejects.toMatchObject({
            status: 500,
            message: "Network Error"
        });
    });
});