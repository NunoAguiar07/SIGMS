import {Day} from "../types/components/Day";
import {View, Text} from "react-native";
import {styles} from "../../css_styling/calendar/Props";

export const DayComponent = ({ day, events }: Day) => (
    <View style={styles.daySection}>
        <Text style={styles.dayTitle}>{day}</Text>
        {events.length > 0 && (
            <View style={styles.eventBlock}>
                {events.map((event, index) => (
                    <View key={index} style={styles.eventItem}>
                        <Text style={styles.eventTime}>{event.time}</Text>
                        <Text style={styles.eventTitle}>{event.title}</Text>
                    </View>
                ))}
            </View>
        )}
    </View>
);