import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";
import {LectureType} from "../../types/welcome/FormCreateEntityValues";


/**
 * Creates a new lecture for a specific school class.
 * @param {number} schoolClassId - The ID of the school class for which the lecture is created.
 * @param {number} roomId - The ID of the room where the lecture takes place.
 * @param {LectureType} type - The type of the lecture (e.g., LECTURE, EXERCISE).
 * @param {number} weekDay - The day of the week when the lecture occurs (0-6 for Sunday-Saturday).
 * @param {string} startTime - The start time of the lecture in HH:mm format.
 * @param {string} endTime - The end time of the lecture in HH:mm format.
 * @returns {Promise<boolean>} - Returns true if the lecture was created successfully, otherwise throws an error.
 */
export const createLecture = async (
    schoolClassId: number,
    roomId: number,
    type: LectureType,
    weekDay: number,
    startTime: string,
    endTime: string
): Promise<boolean> => {
    try {
        const response = await api.post('lectures/add', {
            schoolClassId,
            roomId,
            type,
            weekDay,
            startTime,
            endTime
        }, { withCredentials: true });
        return response.status === 201;
    } catch (error) {
        throw handleAxiosError(error);
    }
};