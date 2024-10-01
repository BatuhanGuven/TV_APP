import java.net.ServerSocket

data class UIConfigurationResponse(
    val UIConfiguration: UIConfiguration
)
data class UIConfiguration(
    val AnalogClock: AnalogClock,
    val DateTimeSettings: DateTimeSettings,
    val LeftSideBox: LeftSideBox,
    val ScrollingSubtitles: ScrollingSubtitles,
    val VideoPlayer: VideoPlayer,
    val ServerIp : ServerIp
)
data class ServerIp (
    val ip : String
)
data class AnalogClock(
    val timeZone: TimeZone,
    val refreshRate: RefreshRate,
    val clockAppearance: ClockAppearance,
    val hands: Hands,
    val text: Text
)

data class TimeZone(
    val value: String,
    val description: String
)

data class RefreshRate(
    val value: Int,
    val unit: String,
    val description: String
)

data class ClockAppearance(
    val size: Size,
    val backgroundColor: BackgroundColor,
    val circle: Circle
)

data class Size(
    val value: String,
    val description: String
)

data class BackgroundColor(
    val value: String,
    val description: String
)

data class Circle(
    val borderColor: String,
    val borderWidth: String,
    val radius: String,
    val description: String
)

data class Hands(
    val hourHand: Hand,
    val minuteHand: Hand,
    val secondHand: Hand
)

data class Hand(
    val color: String,
    val length: String,
    val width: String,
    val description: String
)

data class Text(
    val city: City,
    val timeDisplay: TimeDisplay,
    val country: Country
)

data class City(
    val value: String,
    val fontSize: String,
    val fontWeight: String,
    val color: String,
    val description: String
)

data class TimeDisplay(
    val format: String,
    val fontSize: String,
    val fontWeight: String,
    val description: String
)

data class Country(
    val value: String,
    val fontSize: String,
    val color: String,
    val description: String
)

data class DateTimeSettings(
    val timeFormat: TimeFormat,
    val dateFormat: DateFormat
)

data class TimeFormat(
    val pattern: String,
    val locale: String,
    val timeZone: String,
    val description: String
)

data class DateFormat(
    val pattern: String,
    val locale: String,
    val description: String
)

data class LeftSideBox(
    val box: Box,
    val image: Image,
    val analogClock: AnalogClockModifier
)

data class Box(
    val width: String,
    val height: String,
    val backgroundColor: String,
    val padding: String,
    val description: String
)

data class Image(
    val url: String,
    val contentDescription: String,
    val modifier: ImageModifier,
    val description: String
)

data class ImageModifier(
    val fillWidth: String,
    val padding: String
)

data class Padding(
    val value: String,
    val description: String
)

data class AnalogClockModifier(
    val modifier: Modifier
)

data class Modifier(
    val padding: String,
    val description: String
)

data class ScrollingSubtitles(
    val subtitles: Subtitles,
    val scrollSettings: ScrollSettings,
    val appearance: Appearance,
    val positioning: Positioning
)

data class Subtitles(
    val list: List<String>,
    val description: String
)

data class ScrollSettings(
    val scrollSpeed: ScrollSpeed
)

data class ScrollSpeed(
    val value: Int,
    val description: String
)

data class Appearance(
    val textColor: TextColor,
    val backgroundColor: BackgroundColor,
    val textSize: TextSize,
    val padding: Padding
)

data class TextColor(
    val value: String,
    val description: String
)

data class TextSize(
    val value: String,
    val description: String
)

data class Positioning(
    val horizontalAlignment: HorizontalAlignment,
    val verticalAlignment: VerticalAlignment
)

data class HorizontalAlignment(
    val value: String,
    val description: String
)

data class VerticalAlignment(
    val value: String,
    val description: String
)
data class VideoPlayer(
    val width: String,
    val height: String
)