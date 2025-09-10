import Foundation
import UIKit

class NetworkService: ObservableObject {
    static let shared = NetworkService()
    
    private init() {}
    
    func processMessageContent(_ content: String) -> [ProcessedLink] {
        var results: [ProcessedLink] = []
        let text = content
        
        if let linkData = parseSpecialLinkFormat(text) {
            let beforeLink = String(text[..<linkData.range.lowerBound])
            let afterLink = String(text[linkData.range.upperBound...])
            
            if !beforeLink.isEmpty {
                results.append(ProcessedLink(displayText: beforeLink, actualURL: "", isLink: false))
            }
            
            results.append(ProcessedLink(
                displayText: linkData.displayURL,
                actualURL: linkData.actualURL,
                isLink: true
            ))
            
            if !afterLink.isEmpty {
                results.append(ProcessedLink(displayText: afterLink, actualURL: "", isLink: false))
            }
            
            return results
        } else if let urlRange = detectStandardURL(text) {
            let beforeURL = String(text[..<urlRange.lowerBound])
            let urlText = String(text[urlRange])
            let afterURL = String(text[urlRange.upperBound...])
            
            if !beforeURL.isEmpty {
                results.append(ProcessedLink(displayText: beforeURL, actualURL: "", isLink: false))
            }
            
            results.append(ProcessedLink(
                displayText: urlText,
                actualURL: urlText,
                isLink: true
            ))
            
            if !afterURL.isEmpty {
                results.append(ProcessedLink(displayText: afterURL, actualURL: "", isLink: false))
            }
            
            return results
        }
        
        return [ProcessedLink(displayText: content, actualURL: "", isLink: false)]
    }
    
    private func parseSpecialLinkFormat(_ text: String) -> (displayURL: String, actualURL: String, range: Range<String.Index>)? {
        let pattern = "<([^\\|]+)\\|([^>]+)>"
        
        guard let regex = try? NSRegularExpression(pattern: pattern, options: []) else {
            return nil
        }
        
        let matches = regex.matches(in: text, options: [], range: NSRange(location: 0, length: text.utf16.count))
        
        guard let match = matches.first,
              let actualURLRange = Range(match.range(at: 1), in: text),
              let displayURLRange = Range(match.range(at: 2), in: text),
              let fullRange = Range(match.range, in: text) else {
            return nil
        }
        
        let actualURL = String(text[actualURLRange])
        let displayURL = String(text[displayURLRange])
        
        return (displayURL: displayURL, actualURL: actualURL, range: fullRange)
    }
    
    private func detectStandardURL(_ text: String) -> Range<String.Index>? {
        let pattern = "https?://[^\\s]+"
        
        guard let regex = try? NSRegularExpression(pattern: pattern, options: []) else {
            return nil
        }
        
        let matches = regex.matches(in: text, options: [], range: NSRange(location: 0, length: text.utf16.count))
        
        guard let match = matches.first,
              let range = Range(match.range, in: text) else {
            return nil
        }
        
        return range
    }
    
    func openURL(_ urlString: String) {
        guard let url = URL(string: urlString) else { return }
        
        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
}
