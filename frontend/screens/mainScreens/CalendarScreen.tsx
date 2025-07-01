import {ScrollView, Text, TouchableOpacity, View} from "react-native";
import {styles} from "../css_styling/calendar/Props";
import {CalendarScreenType} from "../types/CalendarScreenType";
import {DayType} from "../types/components/DayType";



const CalendarScreen = ({ schedule, onClickProfile, onClickRoom }: CalendarScreenType) => {

    const getEventsForDay = (dayName: string) => {
        // @ts-ignore
        return schedule.filter(item => item.lecture.weekDay === dayName)
            .map(item => ({
                time: `${item.lecture.startTime} ➜ ${item.lecture.endTime}`,
                title: `${item.lecture.schoolClass.subject.name} (${item.lecture.type}), ${item.lecture.schoolClass.name}: ${item.lecture.room.name}`,
                roomId: item.lecture.room.id, // add this
                teachers: item.teacher.map(t => ({ name: t.user.username, id: t.user.id })),
            }));
    };


    return (
        <View style={styles.container}>
            <View style={styles.cardContainer}>
                <ScrollView style={styles.content} contentContainerStyle={{height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                    <DaySection day="Monday"  events={getEventsForDay("MONDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Tuesday"  events={getEventsForDay("TUESDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Wednesday"  events={getEventsForDay("WEDNESDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Thursday"  events={getEventsForDay("THURSDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Friday"  events={getEventsForDay("FRIDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Saturday"  events={getEventsForDay("SATURDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                </ScrollView>
            </View>
        </View>
    );
};

const DaySection = ({ day, events, onClickProfile, onClickRoom } : DayType) => (
    <View style={styles.daySection}>
        <Text style={styles.dayTitle}>{day}</Text>
        {events.length > 0 && (
            <View style={styles.eventBlock}>
                {events.map((event: any, index: number) => (
                    <View key={index} style={styles.eventItem}>
                        <Text style={styles.eventTime}>{event.time}</Text>
                        <Text style={styles.eventTitle}>{event.title}</Text>


                        <Text style={styles.teacherNames}>
                            Teachers: {event.teachers.map((t: { name: string; }) => t.name).join(', ')}
                        </Text>


                        <View style={styles.teacherButtonRow}>
                            {event.teachers.map((teacher: any, i: number) => (
                                <TouchableOpacity
                                    key={i}
                                    style={styles.teacherCard}
                                    onPress={() => onClickProfile(teacher.id)}
                                >
                                    <Text style={styles.teacherCardText}>👨‍🏫 {teacher.name}</Text>
                                </TouchableOpacity>
                            ))}

                            {/* Room Reports button */}
                            <TouchableOpacity
                                style={styles.teacherCard}
                                onPress={() => onClickRoom(event.roomId)}
                            >
                                <Text style={styles.teacherCardText}>📄 Room Reports</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                ))}
            </View>
        )}
    </View>
);

export default CalendarScreen;