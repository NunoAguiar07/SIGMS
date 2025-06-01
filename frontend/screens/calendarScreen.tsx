import {ScrollView, Text, View} from "react-native";
import {styles} from "../css_styling/calendar/Props";

// @ts-ignore
const calendarScreen = ({ schedule }) => {
    const getEventsForDay = (dayName:any) => {
        console.log(schedule)
        const events = schedule.lectures
            .filter ((item: { weekDay: any; }) => item.weekDay === dayName)
            .map((item: {
                schoolClass: any; startTime: any; endTime: any; type: any; room: any;
            }) => ({
                time: `${item.startTime} ➜ ${item.endTime}`,
                title: `${item.schoolClass.subject.name} ( ${item.type} ), ${item.schoolClass.name}: ${item.room.name}`
            }));
        console.log(events)
        return events
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

/*
// @ts-ignore
const DaySection = ({ day, date, events }) => (
    <View style={styles.daySection}>
        <Text style={styles.dayTitle}>{day}</Text>
        <Text style={styles.daySubtitle}>{date}</Text>
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
);*/
// @ts-ignore
const DaySection = ({ day, events }) => (
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

export default calendarScreen;