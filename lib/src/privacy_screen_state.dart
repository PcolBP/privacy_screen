import 'dart:ui' show Color;

import 'package:flutter/foundation.dart';
import 'privacy_helpers.dart';

@immutable
class PrivacyScreenState {
  final PrivacyIosOptions iosOptions;
  final PrivacyAndroidOptions androidOptions;
  final PrivacyBlurEffect blurEffect;
  final Color backgroundColor;
  final Color? backgroundColorDark;

  const PrivacyScreenState({
    this.iosOptions = const PrivacyIosOptions(),
    this.androidOptions = const PrivacyAndroidOptions(),
    this.blurEffect = PrivacyBlurEffect.extraLight,
    this.backgroundColor = const Color(0xFFFFFFFF),
    this.backgroundColorDark,
  });

  PrivacyScreenState copyWith({
    bool? shouldLock,
    PrivacyIosOptions? iosOptions,
    PrivacyAndroidOptions? androidOptions,
    PrivacyBlurEffect? blurEffect,
    Color? backgroundColor,
    Color? backgroundColorDark,
  }) {
    return PrivacyScreenState(
      androidOptions: androidOptions ?? this.androidOptions,
      iosOptions: iosOptions ?? this.iosOptions,
      blurEffect: blurEffect ?? this.blurEffect,
      backgroundColor: backgroundColor ?? this.backgroundColor,
      backgroundColorDark: backgroundColorDark ?? this.backgroundColorDark,
    );
  }
}
