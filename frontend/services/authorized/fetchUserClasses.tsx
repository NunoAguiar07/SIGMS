import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";

export const fetchUserClasses = async (
): Promise<SchoolClassInterface[]> => {
    try {
        const response = await api.get(
             '/userClasses',
            {
                withCredentials: true
            }
        );
        console.log(response.data);
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};