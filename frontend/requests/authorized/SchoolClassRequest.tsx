import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const SchoolClassRequest = (subjectId: any, setClasses: any, setError: any) => {
    return async () => {
        try {
            const response = await api.get(`subjects/${encodeURIComponent(subjectId)}/classes`, { withCredentials: true })
            console.log(response)
            if (response.status === 200) {
                console.log(response.data.data)
                setClasses(response.data.data);
            }
        } catch (error) {
            handleAxiosError(error, setError)
        }
    }
}