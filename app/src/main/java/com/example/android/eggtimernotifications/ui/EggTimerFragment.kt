/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.databinding.FragmentEggTimerBinding
import com.google.firebase.messaging.FirebaseMessaging

class EggTimerFragment : Fragment() {

    private val TOPIC = "breakfast"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEggTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false
        )

        val viewModel = ViewModelProviders.of(this).get(EggTimerViewModel::class.java)

        binding.eggTimerViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        createChannel(
            getString(R.string.egg_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )

        createChannel(
            getString(R.string.breakfast_notification_channel_id),
            getString(R.string.breakfast_notification_channel_name)
        )


        subscribeTopic()

        return binding.root
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.breakfast_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    /**
     * A messaging app can be a good example for the Publish/Subscribe model. Imagine that an app checks for
     * new messages every 10 seconds. This will not only drain your phone battery, but will also use unnecessary
     * network resources, and will create an unnecessary load on your app's server. Instead, a client device can
     * subscribe and be notified when there are new messages delivered through your app.

    Topics allow you to send a message to multiple devices that have opted in to that particular topic.
    For clients, topics are specific data sources which the client is interested in. For the server, topics
    are groups of devices which have opted in to receive updates on a specific data source. Topics can be used
    to present categories of notifications, such as news, weather forecasts, and sports results. For this part
    of the codelab, you will create a "breakfast" topic to remind the interested app users to eat eggs with their
    breakfast.

    To subscribe to a topic, the client app calls the Firebase Cloud Messaging subscribeToTopic(``) function with
    the topic name breakfast. This call can have two outcomes. If the caller succeeds, the OnCompleteListener
    callback will be called with the subscribed message. If the client fails to subscribe, the callback will
    receive an error message instead.

    In your app, you will automatically subscribe your users to the breakfast topic. In most production apps,
    however, it's better to give users control over which topics to subscribe to.
     */

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener { task ->
            val msg = if (task.isSuccessful.not()) {
                getString(R.string.message_subscribe_failed)
            } else {
                getString(R.string.message_subscribed)
            }

            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance() = EggTimerFragment()
    }
}

