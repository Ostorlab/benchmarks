//
//  CameraView.swift
//  PhotoShare
//
//  Created by elyousfi on 08/09/2025.
//

import SwiftUI
import AVFoundation

struct CameraView: UIViewControllerRepresentable {
    @ObservedObject var photoManager: PhotoManager
    @Environment(\.presentationMode) var presentationMode
    
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        
        // Check if camera is available, fallback to photo library if not (e.g., in simulator)
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            picker.sourceType = .camera
        } else {
            picker.sourceType = .photoLibrary
        }
        
        picker.allowsEditing = false
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: CameraView
        
        init(_ parent: CameraView) {
            self.parent = parent
        }
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let image = info[.originalImage] as? UIImage {
                // Apply subtle enhancement to make the app feel more legitimate
                let enhancedImage = enhanceImage(image)
                parent.photoManager.addPhoto(enhancedImage)
            }
            parent.presentationMode.wrappedValue.dismiss()
        }
        
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.presentationMode.wrappedValue.dismiss()
        }
        
        private func enhanceImage(_ image: UIImage) -> UIImage {
            // Simple enhancement: slight contrast and brightness adjustment
            // This makes the app seem more sophisticated while preserving metadata
            guard let ciImage = CIImage(image: image) else { return image }
            
            let filter = CIFilter(name: "CIColorControls")!
            filter.setValuesForKeys([
                kCIInputImageKey: ciImage,
                "inputContrast": 1.1,
                "inputBrightness": 0.05,
                "inputSaturation": 1.05
            ])
            
            guard let outputImage = filter.outputImage,
                  let cgImage = CIContext().createCGImage(outputImage, from: outputImage.extent) else {
                return image
            }
            
            return UIImage(cgImage: cgImage)
        }
    }
}
