import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const SubjectsRequest = ( searchQuery:any, setSubjects: any, setError: any) => {
    return async () => {
        try {
            const url = searchQuery
                ? `subjects?search=${encodeURIComponent(searchQuery)}`
                : 'subjects';
            const response = await api.get(url, { withCredentials: true })
            if(response.status === 200) {
                setSubjects(response.data.data)
            }
        } catch (error) {
            handleAxiosError(error, setError)
        }
    }
}