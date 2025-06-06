import {StudyRoomCapacity} from "../../../types/StudyRoomCapacity";
import {Text, View} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";

const Device = ({studyRoom}: {studyRoom: StudyRoomCapacity}) => {
    return (<View style={commonStyles.container}>
            <Text>Sala {studyRoom.roomName}</Text>
            <Text>{studyRoom.capacity}</Text>
    </View>)
}

export default Device;