import api from "../interceptors/DeviceInterceptor";


export const SubjectsRequest = ( searchQuery:any, setSubjects: any, setError: any) => {
    return async () => {
        try {
            const url = searchQuery
                ? `/subjects?search=${encodeURIComponent(searchQuery)}`
                : '/subjects';
            const response = await api.get(url, { withCredentials: true })
            if(response.status === 200) {
                console.log(response.data.data)
                setSubjects(response.data.data)
            }
        } catch (error) {
            setError(error)
        }
    }
}