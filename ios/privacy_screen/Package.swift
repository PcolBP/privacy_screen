// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "privacy_screen",
    platforms: [
        .iOS("13.0")
    ],
    products: [
        // If the plugin name contains "_", replace with "-" for the library name.
        .library(name: "privacy-screen", targets: ["privacy_screen"])
    ],
    dependencies: [],
    targets: [
        .target(
            name: "privacy_screen",
            dependencies: []
        )
    ]
)
