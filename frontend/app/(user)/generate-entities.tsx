import {AdminEntityCreationScreen} from "../../screens/mainScreens/GenerateEntitiesScreen";
import ErrorHandler from "../(public)/error";
import {useAdminEntityCreation} from "../../hooks/useAdminEntityCreation";
import {BackgroundImage} from "../../screens/components/BackgroundImage";


const AdminEntityCreation = () => {
    const {
        selectedEntity,
        setSelectedEntity,
        formValues,
        setFormValues,
        error,
        subjects,
        subjectClasses,
        rooms,
        searchQuery,
        setSearchQuery,
        isLoading,
        handleSubmit,
    } = useAdminEntityCreation();

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <BackgroundImage>
            <AdminEntityCreationScreen
                selectedEntity={selectedEntity}
                onEntitySelect={setSelectedEntity}
                formValues={formValues}
                setFormValues={setFormValues}
                onSubmit={handleSubmit}
                subjects={subjects}
                subjectClasses={subjectClasses}
                rooms={rooms}
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
                isLoading={isLoading}
            />
        </BackgroundImage>
    );
};

export default AdminEntityCreation;