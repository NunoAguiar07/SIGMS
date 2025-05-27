import {useEffect, useState} from "react";
import {RoomInterface} from "../../interfaces/RoomInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {useDebounce} from "use-debounce";
import {getRooms} from "../../requests/authorized/GetRooms";
import {CreateReportScreen} from "../../screens/CreateReportScreen";
import {SubjectInterface} from "../../interfaces/SubjectInterface";
import ErrorHandler from "../(public)/error";
import {CreateIssueReportRequest} from "../../requests/authorized/CreateIssueReport";
import {Alert} from "react-native";
import {GetIssuesByRoomRequest} from "../../requests/authorized/GetIssueReports";

const Rooms = () => {
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [reportText, setReportText] = useState('');


    useEffect(() => {
        if(debouncedSearchQuery.trim().length > 0) {
            const loadRooms = getRooms(debouncedSearchQuery, setRooms, setError);
            loadRooms();
        } else {
            setRooms([]);
            setError(null);
            setSelectedRoom(null);
        }

    }, [debouncedSearchQuery]);

    const handleRoomSelect = (room: RoomInterface) => {
        setSelectedRoom(room);
        setReportText('');
    };

    const handleSubmitReport = async (roomId: number, report: string) => {
        const submit = CreateIssueReportRequest(roomId, report, setError);
        const success = await submit();

        if (success) {
            Alert.alert('Success', 'Report submitted successfully!');
            setSelectedRoom(null);
            setReportText('');
        } else {
            Alert.alert('Error', 'Failed to submit the report. Please try again.');
        }
    };

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    return (
        <CreateReportScreen
         rooms={rooms}
            selectedRoom={selectedRoom}
            reportText={reportText}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            onRoomSelect={handleRoomSelect}
            onReportTextChange={setReportText}
            onSubmitReport={handleSubmitReport}
        ></CreateReportScreen>

    );
};



export default Rooms;