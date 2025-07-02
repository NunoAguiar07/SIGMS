import api from "../interceptors/DeviceInterceptor";
import {StudyRoomCapacity} from "../../types/StudyRoomCapacity";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Fetches the study room capacities from the API.
 *
 * @returns {Promise<StudyRoomCapacity[]>} A promise that resolves to an array of study room capacities.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const fetchStudyRoomCapacity = async (): Promise<StudyRoomCapacity[]> => {
    try {
        const response = await api.get('/devices', { withCredentials: true });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};