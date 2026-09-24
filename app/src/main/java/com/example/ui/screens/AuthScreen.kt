package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TaskViewModel
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40
import com.example.ui.theme.Indigo60
import com.example.ui.theme.Sky40

@Composable
fun AuthScreen(
  viewModel: TaskViewModel,
  onAuthSuccess: () -> Unit
) {
  val context = LocalContext.current
  val isAuthLoading by viewModel.isAuthLoading.collectAsState()
  val authError by viewModel.authError.collectAsState()
  val phoneOtpSent by viewModel.phoneOtpSent.collectAsState()
  val generatedOtp by viewModel.generatedOtp.collectAsState()

  // 0 = Mobile Phone, 1 = Email
  var authMethod by remember { mutableIntStateOf(0) }
  // 0 = Sign In / Login, 1 = Create Account
  var selectedTab by remember { mutableIntStateOf(0) }

  // Phone auth inputs
  var phoneNumber by remember { mutableStateOf("") }
  var phoneOtp by remember { mutableStateOf("") }
  var phoneDisplayName by remember { mutableStateOf("") }

  // Email auth inputs
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var displayName by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background
          )
        )
      )
      .statusBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 480.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Icon & Hero Title
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(Indigo40, Sky40)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Daily Tasks Logo",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Daily Tasks",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "Secure Account & Cloud Sync",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Auth Card
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Method Selector: Mobile Phone vs Email
            TabRow(
              selectedTabIndex = authMethod,
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .height(44.dp)
            ) {
              Tab(
                selected = authMethod == 0,
                onClick = {
                  authMethod = 0
                  viewModel.clearAuthError()
                },
                text = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      Icons.Default.Phone,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "Mobile Phone",
                      fontWeight = if (authMethod == 0) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 13.sp
                    )
                  }
                },
                modifier = Modifier.testTag("tab_method_phone")
              )
              Tab(
                selected = authMethod == 1,
                onClick = {
                  authMethod = 1
                  viewModel.clearAuthError()
                },
                text = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      Icons.Default.Email,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "Email",
                      fontWeight = if (authMethod == 1) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 13.sp
                    )
                  }
                },
                modifier = Modifier.testTag("tab_method_email")
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs: Sign In vs Create Account
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                shadowElevation = if (selectedTab == 0) 2.dp else 0.dp,
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
              ) {
                TextButton(
                  onClick = {
                    selectedTab = 0
                    viewModel.clearAuthError()
                    viewModel.resetPhoneOtpState()
                  },
                  modifier = Modifier.testTag("subtab_sign_in")
                ) {
                  Text(
                    text = "Sign In",
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                shadowElevation = if (selectedTab == 1) 2.dp else 0.dp,
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
              ) {
                TextButton(
                  onClick = {
                    selectedTab = 1
                    viewModel.clearAuthError()
                    viewModel.resetPhoneOtpState()
                  },
                  modifier = Modifier.testTag("subtab_sign_up")
                ) {
                  Text(
                    text = "Create Account",
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Error message banner
            AnimatedVisibility(visible = authError != null) {
              authError?.let { err ->
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                ) {
                  Text(
                    text = err,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                  )
                }
              }
            }

            // ==================== PHONE AUTH FORM ====================
            if (authMethod == 0) {
              // Full Name (if Create Account)
              if (selectedTab == 1) {
                OutlinedTextField(
                  value = phoneDisplayName,
                  onValueChange = {
                    phoneDisplayName = it
                    viewModel.clearAuthError()
                  },
                  label = { Text("Full Name") },
                  placeholder = { Text("e.g. Lakshman Biswas") },
                  leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_phone_name_input"),
                  keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
              }

              // Mobile Number field
              OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                  phoneNumber = it
                  viewModel.clearAuthError()
                },
                label = { Text("Mobile Number") },
                placeholder = { Text("e.g. +1 555-0123 or 9876543210") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                enabled = !phoneOtpSent,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Phone,
                  imeAction = if (phoneOtpSent) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    if (!phoneOtpSent && phoneNumber.isNotBlank()) {
                      viewModel.sendPhoneOtp(phoneNumber)
                    }
                  }
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_phone_input")
              )

              // If OTP sent: show OTP banner and OTP input field
              if (phoneOtpSent) {
                Spacer(modifier = Modifier.height(14.dp))

                // Test OTP Hint Card
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = Emerald40.copy(alpha = 0.12f)
                  ),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "SMS Code Sent to $phoneNumber",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Emerald40
                      )
                      Text(
                        text = "Verification code: ${generatedOtp ?: "123456"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                    TextButton(
                      onClick = {
                        phoneOtp = generatedOtp ?: "123456"
                      },
                      modifier = Modifier.testTag("autofill_otp_btn")
                    ) {
                      Text("Auto-fill", fontWeight = FontWeight.Bold, color = Emerald40)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // OTP Input Field
                OutlinedTextField(
                  value = phoneOtp,
                  onValueChange = {
                    if (it.length <= 6) {
                      phoneOtp = it
                      viewModel.clearAuthError()
                    }
                  },
                  label = { Text("6-Digit Verification Code") },
                  placeholder = { Text("123456") },
                  leadingIcon = { Icon(Icons.Default.Sms, contentDescription = null) },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                  ),
                  keyboardActions = KeyboardActions(
                    onDone = {
                      if (phoneOtp.length == 6) {
                        viewModel.verifyPhoneOtpAndSignIn(
                          phoneNumber = phoneNumber,
                          code = phoneOtp,
                          name = phoneDisplayName,
                          isSignUp = selectedTab == 1,
                          onSuccess = onAuthSuccess
                        )
                      }
                    }
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_phone_otp_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  TextButton(
                    onClick = { viewModel.resetPhoneOtpState() },
                    modifier = Modifier.testTag("change_phone_number_btn")
                  ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Change Number", style = MaterialTheme.typography.bodySmall)
                  }

                  TextButton(
                    onClick = { viewModel.sendPhoneOtp(phoneNumber) },
                    modifier = Modifier.testTag("resend_otp_btn")
                  ) {
                    Text("Resend Code", style = MaterialTheme.typography.bodySmall)
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Phone Action Button
              if (!phoneOtpSent) {
                Button(
                  onClick = {
                    viewModel.sendPhoneOtp(phoneNumber)
                  },
                  enabled = !isAuthLoading && phoneNumber.replace(Regex("[^0-9+]"), "").length >= 7,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("phone_send_otp_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Indigo40)
                ) {
                  if (isAuthLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                  } else {
                    Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = if (selectedTab == 0) "Send Login OTP" else "Send Verification OTP",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              } else {
                Button(
                  onClick = {
                    viewModel.verifyPhoneOtpAndSignIn(
                      phoneNumber = phoneNumber,
                      code = phoneOtp,
                      name = phoneDisplayName,
                      isSignUp = selectedTab == 1,
                      onSuccess = onAuthSuccess
                    )
                  },
                  enabled = !isAuthLoading && phoneOtp.length == 6,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("phone_verify_login_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Emerald40)
                ) {
                  if (isAuthLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                  } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = if (selectedTab == 0) "Verify & Log In" else "Create Account & Log In",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            // ==================== EMAIL AUTH FORM ====================
            if (authMethod == 1) {
              // Name field (if Sign Up)
              if (selectedTab == 1) {
                OutlinedTextField(
                  value = displayName,
                  onValueChange = { displayName = it },
                  label = { Text("Full Name") },
                  leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_name_input"),
                  keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
              }

              // Email field
              OutlinedTextField(
                value = email,
                onValueChange = {
                  email = it
                  viewModel.clearAuthError()
                },
                label = { Text("Email Address") },
                placeholder = { Text("you@example.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Email,
                  imeAction = ImeAction.Next
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_email_input")
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Password field
              OutlinedTextField(
                value = password,
                onValueChange = {
                  password = it
                  viewModel.clearAuthError()
                },
                label = { Text("Password") },
                placeholder = { Text("At least 6 characters") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                  IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.testTag("toggle_password_btn")
                  ) {
                    Icon(
                      imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = "Toggle password visibility"
                    )
                  }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Password,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    if (selectedTab == 0) {
                      viewModel.signInWithEmail(email, password, onAuthSuccess)
                    } else {
                      viewModel.signUpWithEmail(email, password, displayName, onAuthSuccess)
                    }
                  }
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_password_input")
              )

              Spacer(modifier = Modifier.height(20.dp))

              // Submit Button
              Button(
                onClick = {
                  if (selectedTab == 0) {
                    viewModel.signInWithEmail(email, password, onAuthSuccess)
                  } else {
                    viewModel.signUpWithEmail(email, password, displayName, onAuthSuccess)
                  }
                },
                enabled = !isAuthLoading && email.isNotBlank() && password.length >= 6,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("auth_submit_btn"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Indigo40
                )
              ) {
                if (isAuthLoading) {
                  CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                  )
                } else {
                  Text(
                    text = if (selectedTab == 0) "Sign In" else "Create Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Divider
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              HorizontalDivider(modifier = Modifier.weight(1f))
              Text(
                text = "OR",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
              )
              HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Google Sign-In Button
            OutlinedButton(
              onClick = {
                viewModel.signInWithGoogle(context, onAuthSuccess)
              },
              enabled = !isAuthLoading,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_google_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Sky40,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Continue with Google")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Guest Demo Button
            OutlinedButton(
              onClick = {
                viewModel.signInAsGuest(onAuthSuccess)
              },
              enabled = !isAuthLoading,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_guest_btn"),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Emerald40
              )
            ) {
              Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = Emerald40,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Quick Guest Explorer Mode")
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security / Firebase feature info pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = Indigo60,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Per-user isolated storage. Supports Mobile Phone OTP, Google Firebase Auth, and Cloud Firestore sync seamlessly.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
