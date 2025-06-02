import {useEffect, useState} from "react";
import {RoomInterface} from "../types/RoomInterface";
import {useDebounce} from "use-debounce";
import {fetchRooms} from "../services/authorized/fetchRooms";
import {CreateIssueReportRequest} from "../services/authorized/CreateIssueReport";
import {Alert} from "react-native";
import {ParsedError} from "../types/errors/ParseErrorTypes";


export const useCreateReport = () => {
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [reportText, setReportText] = useState('');
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [error, setError] = useState<ParsedError | null>(null);

    useEffect(() => {
        if (debouncedSearchQuery.trim().length > 0) {
            fetchRooms(debouncedSearchQuery)
                .then((data) => {
                    setRooms(data);
                }).catch(err => {
                    setError(err as ParsedError);
                    setRooms([]);
                    setSelectedRoom(null);
            })
        } else {
            setRooms([]);
            setSelectedRoom(null);
            setError(null);
        }
    }, [debouncedSearchQuery]);

    const handleRoomSelect = (room: RoomInterface) => {
        setSelectedRoom(room);
        setReportText('');
    };

    const handleSubmitReport = async (roomId: number, description: string) => {
        const submit = CreateIssueReportRequest(roomId, description);
        const success = await submit;

        if (success) {
            Alert.alert('Success', 'Report submitted successfully!');
            setSelectedRoom(null);
            setReportText('');
        } else {
            Alert.alert('Error', 'Failed to submit the report. Please try again.');
        }
    };

    return {
        rooms,
        selectedRoom,
        reportText,
        searchQuery,
        error,
        setSearchQuery,
        setReportText,
        handleRoomSelect,
        handleSubmitReport
    };
};