package io.github.ikarenkov.workshop.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import io.github.ikarenkov.workshop.screens.profile_setup.SetupStepScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class TrainingRecommendationsScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : SetupStepScreen {

    override val title: String
        get() = "Training Recommendations 🎁"

    @Composable
    override fun Content(modifier: Modifier) {
        TrainingRecommendationsContent(modifier)
    }
}

val recommendations
    @Composable
    get() = buildAnnotatedString {
        withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append("We created a ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("training plan")
            }
            append("for you based on your climbing level and goals. Check it out!\n\n")
        }

        withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) {
            append("🤝\uD83E\uDD1D\uD83E\uDD1D Beginner Boulder Buddy\n")
        }
        withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 20.sp))) {
            append("   \u2022 Task: Hug every hold like it's your long\u2022lost friend.\n")
            append("   \u2022 Goal: Avoid looking down—gravity is just a theory anyway!\n\n")
        }

        withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) {
            append("🐱🐱🐱 Intermediate Wall Whisperer\n")
        }
        withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 20.sp))) {
            append("   \u2022 Task: Try to convince the wall to cooperate. If it refuses, bribe it with chalk.\n")
            append("   \u2022 Goal: Make at least one dramatic fall, so everyone knows you're pushing your limits.\n\n")
        }

        withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) {
            append("🧙🧙🧙 Advanced Route Wizard\n")
        }
        withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 20.sp))) {
            append("   \u2022 Task: Climb so smoothly that the holds start thinking you're Spider\u2022Man.\n")
            append("   \u2022 Goal: Finish every route with a victory dance—bonus points if it's on the top hold.\n\n")
        }

        withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) {
            append("💪💪💪 Pro Crimp Crusher\n")
        }
        withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 20.sp))) {
            append("   \u2022 Task: Pretend like crimps are just tiny jugs, and you're definitely not feeling the burn.\n")
            append("   \u2022 Goal: Laugh maniacally at gravity's futile attempts to bring you down.\n\n")
        }

        withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) {
            append("😈😈😈 Legendary Dyno Daredevil\n")
        }
        withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 20.sp))) {
            append("   \u2022 Task: Leap to the next hold as if you're auditioning for an action movie.\n")
            append("   \u2022 Goal: Stick the landing with a grin that says, \"I was born for this.\"\n")
        }
    }

@Composable
fun TrainingRecommendationsContent(
    modifier: Modifier = Modifier
) {
    Text(
        text = recommendations,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    )
}

@Preview
@Composable
private fun PreviewTrainingRecommendations() {
    TrainingRecommendationsContent(Modifier.fillMaxSize())
}