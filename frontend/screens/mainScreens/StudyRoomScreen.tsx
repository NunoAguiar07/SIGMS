import {Text, TouchableOpacity, View} from "react-native";
import {Device} from "../components/studyRooms/Device";
import {StudyRoomProps} from "../../types/StudyRoomProps";
import { getColumnStyle} from "../css_styling/common/Containers";
import {TextStyles} from "../css_styling/common/Text";

const StudyRoomScreen = ({studyRooms, forceUpdate}: StudyRoomProps) => {
    let update = true
    const setUpdate = () => {
        forceUpdate(update);
        update = !update;
    }
    const chunkedStudyRooms = chunkArray(studyRooms, 3)
    return (
        <View style={{alignContent: 'center', justifyContent: 'center', width: '100%', height: '100%'}}>
            {chunkedStudyRooms.map((chunk) => (
                <View style={getColumnStyle(100/chunkedStudyRooms.length,10)}>
                    {chunk.map((studyRoom) => (
                        <Device studyRoom={studyRoom} />
                    ))}
                </View>
            ))}
            <TouchableOpacity onPress={setUpdate}>
                <Text style={[TextStyles.bold, TextStyles.h2]}>Refresh</Text>
            </TouchableOpacity>
        </View>
    );
}

function chunkArray<T>(arr: T[], size: number): T[][] {
    if (size <= 0) {
        throw new Error('Chunk size must be greater than 0');
    }

    if (arr.length === 0) {
        return [];
    }

    const chunks: T[][] = [];
    for (let i = 0; i < arr.length; i += size) {
        chunks.push(arr.slice(i, i + size));
    }
    return chunks;
}
export default StudyRoomScreen;
