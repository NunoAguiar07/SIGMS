import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";
import {Lecture} from "../../types/calendar/Lecture";


/**
 * Updates the schedule of a lecture with new values and effective dates.
 *
 * @param {Lecture} lecture - The lecture object containing updated values.
 * @param {string | null} effectiveFrom - The start date from which the changes should take effect.
 * @param {string | null} effectiveUntil - The end date until which the changes should be valid.
 * @returns {Promise<boolean>} A promise that resolves to true if the update was successful, otherwise false.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const updateLectureSchedule = async ( lecture : Lecture, effectiveFrom :string | null, effectiveUntil:string | null): Promise<boolean> => {
    try {
        const response = await api.put(
            `lectures/${encodeURIComponent(lecture.id)}/update`,
            {
                newRoomId: lecture.room.id,
                newType: lecture.type,
                newWeekDay: lecture.weekDay,
                newStartTime: lecture.startTime,
                newEndTime: lecture.endTime,
                changeStartDate: effectiveFrom,
                changeEndDate: effectiveUntil
            },
            { withCredentials: true }
        );
        return response.status === 200;
    } catch (error) {
        throw handleAxiosError(error);
    }
}
