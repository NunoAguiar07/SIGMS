import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";


export const fetchSubjectClasses = async (subjectId: number): Promise<SchoolClassInterface[]> => {
    try {
        const response = await api.get(`/subjects/${encodeURIComponent(subjectId)}/classes`, { withCredentials: true });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};