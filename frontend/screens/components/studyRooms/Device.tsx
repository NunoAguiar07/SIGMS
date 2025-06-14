import {StudyRoomCapacity} from "../../../types/StudyRoomCapacity";
import {Text, View} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";

export const Device = ({studyRoom}: {studyRoom: StudyRoomCapacity}) => {
    return (<View style={commonStyles.card}>
            <Text>Sala {studyRoom.roomName}</Text>
            <Text>{studyRoom.capacity}</Text>
    </View>)
}
