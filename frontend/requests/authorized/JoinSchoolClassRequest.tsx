import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const JoinSchoolClassRequest  = (subjectId: number, classId: number, setError: any) => {
    return async () => {
        try {
            const response = await api.post(`/subjects/${encodeURIComponent(subjectId)}/classes/${encodeURIComponent(classId)}/users/add`, { withCredentials: true })
            console.log(response)
            if (response.status === 200) {
                console.log(response.data.data)
                return true;
            }
        } catch (error) {
            handleAxiosError(error, setError)
        }
    }
}