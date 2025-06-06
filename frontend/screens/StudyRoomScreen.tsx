import {StudyRoomCapacity} from "../types/StudyRoomCapacity";
import {commonStyles} from "../css_styling/common/CommonProps";
import {TouchableOpacity, View} from "react-native";
import Device from "./components/studyRooms/Device";
import {StudyRoomProps} from "../types/StudyRoomProps";

const StudyRoomScreen = ({studyRooms, forceUpdate}: StudyRoomProps) => {
    let update = true
    console.log(studyRooms)
    const setUpdate = () => {
        forceUpdate(update);
        update = !update;
    }
    return (
        <View style={commonStyles.container}>
            {studyRooms.map((studyRoom) => (
                <Device studyRoom={studyRoom} />
            ))}
            <TouchableOpacity onPress={setUpdate}>
                Refresh
            </TouchableOpacity>
        </View>
    );
}

export default StudyRoomScreen;
