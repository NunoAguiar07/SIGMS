import api from "../interceptors/DeviceInterceptor";


export const SubjectsRequest = ( setSubjects: any, setError: any) => {
    return async () => {
        try {
            const response = await api.get(`/subject`, { withCredentials: true })
            if(response.status === 200) {
                setSubjects(response.data)
            }
        } catch (error) {
            setError(error)
        }
    }
}