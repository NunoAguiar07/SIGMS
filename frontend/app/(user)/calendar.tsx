import React from "react";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useSchedule} from "../../hooks/useSchedule";
import CalendarScreen from "../../screens/mainScreens/CalendarScreen";
import {BackgroundImage} from "../../screens/components/BackgroundImage";
import {View} from "react-native";
import {ContainerStyles} from "../../screens/css_styling/common/Containers";

const Calendar = () => {
    const { schedule, error, loading, onClickProfile, onClickRoom } = useSchedule();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <BackgroundImage>
            <View style={[ContainerStyles.container, {height: '100%', justifyContent: 'center', alignItems: 'center'}]}>
                <View style={{height: '80%', width: '100%', alignItems: 'center'}}>
                    <CalendarScreen schedule={schedule} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />;
                </View>
            </View>
        </BackgroundImage>
    );
};

export default Calendar;