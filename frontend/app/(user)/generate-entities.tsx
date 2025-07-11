import {AdminEntityCreationScreen} from "../../screens/mainScreens/GenerateEntitiesScreen";
import ErrorHandler from "../(public)/error";
import {useAdminEntityCreation} from "../../hooks/useAdminEntityCreation";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";


const AdminEntityCreation = () => {
    const {
        selectedEntity,
        setSelectedEntity,
        handleEntitySelect,
        formValues,
        setFormValues,
        error,
        subjects,
        subjectClasses,
        rooms,
        searchQuerySubjects,
        setSearchQuerySubjects,
        searchQueryRooms,
        setSearchQueryRooms,
        isLoading,
        handleSubmit,
        handleItemSelect
    } = useAdminEntityCreation();

    if(isLoading) {
        return <LoadingPresentation />;
    }

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <AdminEntityCreationScreen
            selectedEntity={selectedEntity}
            onEntitySelect={setSelectedEntity}
            handleEntitySelect={handleEntitySelect}
            formValues={formValues}
            setFormValues={setFormValues}
            onSubmit={handleSubmit}
            subjects={subjects}
            subjectClasses={subjectClasses}
            rooms={rooms}
            searchQuerySubjects={searchQuerySubjects}
            setSearchQuerySubjects={setSearchQuerySubjects}
            searchQueryRooms={searchQueryRooms}
            setSearchQueryRooms={setSearchQueryRooms}
            onItemSelect={handleItemSelect}
        />
    );
};

export default AdminEntityCreation;