import {ScrollView, Text, View} from "react-native";
import {styles} from "../css_styling/calendar/Props";
import {CalendarScreenType} from "../types/CalendarScreenType";
import {DayType} from "../types/components/DayType";

const CalendarScreen = ({ schedule }: CalendarScreenType) => {
    const getEventsForDay = (dayName:string) => {
        return schedule.filter((item) => item.weekDay === dayName)
            .map((item) => ({
                time: `${item.startTime} ➜ ${item.endTime}`,
                title: `${item.schoolClass.subject.name} (${item.type}), ${item.schoolClass.name}: ${item.room.name}`,
            }));
    };


    return (
        <View style={styles.container}>
            <View style={styles.cardContainer}>
                <ScrollView style={styles.content}>
                    <DaySection day="Monday"  events={getEventsForDay("MONDAY")} />
                    <DaySection day="Tuesday"  events={getEventsForDay("TUESDAY")} />
                    <DaySection day="Wednesday"  events={getEventsForDay("WEDNESDAY")} />
                    <DaySection day="Thursday"  events={getEventsForDay("THURSDAY")} />
                    <DaySection day="Friday"  events={getEventsForDay("FRIDAY")} />
                    <DaySection day="Saturday"  events={getEventsForDay("SATURDAY")} />
                </ScrollView>
            </View>
        </View>
    );
};

const DaySection = ({ day, events } : DayType) => (
    <View style={styles.daySection}>
        <Text style={styles.dayTitle}>{day}</Text>
        {events.length > 0 && (
            <View style={styles.eventBlock}>
                {events.map((event:any, index:any) => (
                    <View key={index} style={styles.eventItem}>
                        <Text style={styles.eventTime}>{event.time}</Text>
                        <Text style={styles.eventTitle}>{event.title}</Text>
                    </View>
                ))}
            </View>
        )}
    </View>
);

export default CalendarScreen;