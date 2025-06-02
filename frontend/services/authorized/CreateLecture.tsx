import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";
import {LectureType} from "../../types/welcome/FormCreateEntityValues";


export const createLecture = async (
    schoolClassId: number,
    roomId: number,
    type: LectureType,
    weekDay: number,
    startTime: string,
    endTime: string
): Promise<boolean> => {
    try {
        console.log('Creating lecture with values:', {
            schoolClassId,
            roomId,
            type,
            weekDay,
            startTime,
            endTime
        })
        const response = await api.post('lectures/add', {
            schoolClassId,
            roomId,
            type,
            weekDay,
            startTime,
            endTime
        }, { withCredentials: true });
        console.log(response);
        return response.status === 201;
    } catch (error) {
        console.log(error);
        throw handleAxiosError(error);
    }
};