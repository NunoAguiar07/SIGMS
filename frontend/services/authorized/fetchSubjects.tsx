import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {SubjectInterface} from "../../types/SubjectInterface";


export const fetchSubjects = async (searchQuery: string): Promise<SubjectInterface[]> => {
    try {
        const url = searchQuery
            ? `subjects?search=${encodeURIComponent(searchQuery)}`
            : 'subjects';
        const response = await api.get(url, { withCredentials: true });
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};