import {StudyRoomCapacity} from "../../../types/StudyRoomCapacity";
import {Text, View} from "react-native";
import {TextStyles} from "../../css_styling/common/Text";
import {ContainerStyles} from "../../css_styling/common/Containers";

export const Device = ({studyRoom}: {studyRoom: StudyRoomCapacity}) => {
    return (<View style={[ContainerStyles.roundedContainer, ContainerStyles.border, ContainerStyles.borderPrimary, ContainerStyles.backgroundSecondary, ContainerStyles.columnBase]}>
            <Text style={[TextStyles.bold, TextStyles.h1]}>Sala {studyRoom.roomName}</Text>
            <Text style={[TextStyles.regular, TextStyles.h2]}>Capacity: {studyRoom.capacity}</Text>
    </View>)
}
