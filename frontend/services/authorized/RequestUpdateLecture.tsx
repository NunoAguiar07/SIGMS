

import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";
import {Lecture} from "../../types/calendar/Lecture";

export const updateLectureSchedule = async ( lecture : Lecture, effectiveFrom :string | null, effectiveUntil:string | null): Promise<boolean> => {
    try {
        console.log('Updating lecture schedule with values:', {
            lectureId: lecture.id,
            roomId: lecture.room.id,
            type: lecture.type,
            weekDay: lecture.weekDay,
            startTime: lecture.startTime,
            endTime: lecture.endTime,
            effectiveFrom,
            effectiveUntil
        });

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
        console.log('Lecture schedule updated successfully:', response.data);
        return response.status === 200;
    } catch (error) {
        console.log('Error updating lecture schedule:', error);
        throw handleAxiosError(error);
    }
}
