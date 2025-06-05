import {CreateReportScreen} from "../../../screens/mainScreens/CreateReportScreen";
import ErrorHandler from "../../(public)/error";
import {useCreateReport} from "../../../hooks/useCreateReport";

const CreateReport = () => {
    const {
        rooms,
        selectedRoom,
        reportText,
        searchQuery,
        error,
        setSearchQuery,
        setReportText,
        handleRoomSelect,
        handleSubmitReport
    } = useCreateReport();

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

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
        />
    );
};

export default CreateReport;